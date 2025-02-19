package jp.co.soramitsu.runtime.multiNetwork

import javax.inject.Inject
import jp.co.soramitsu.common.mixin.api.UpdatesMixin
import jp.co.soramitsu.common.mixin.api.UpdatesProviderUi
import jp.co.soramitsu.common.utils.diffed
import jp.co.soramitsu.common.utils.inBackground
import jp.co.soramitsu.common.utils.mapList
import jp.co.soramitsu.coredb.dao.ChainDao
import jp.co.soramitsu.coredb.model.chain.ChainNodeLocal
import jp.co.soramitsu.fearless_utils.runtime.RuntimeSnapshot
import jp.co.soramitsu.runtime.multiNetwork.chain.ChainSyncService
import jp.co.soramitsu.runtime.multiNetwork.chain.mapChainLocalToChain
import jp.co.soramitsu.runtime.multiNetwork.chain.mapNodeLocalToNode
import jp.co.soramitsu.runtime.multiNetwork.chain.model.Chain
import jp.co.soramitsu.runtime.multiNetwork.chain.model.ChainId
import jp.co.soramitsu.runtime.multiNetwork.chain.model.NodeId
import jp.co.soramitsu.runtime.multiNetwork.connection.ChainConnection
import jp.co.soramitsu.runtime.multiNetwork.connection.ConnectionPool
import jp.co.soramitsu.runtime.multiNetwork.runtime.RuntimeProvider
import jp.co.soramitsu.runtime.multiNetwork.runtime.RuntimeProviderPool
import jp.co.soramitsu.runtime.multiNetwork.runtime.RuntimeSubscriptionPool
import jp.co.soramitsu.runtime.multiNetwork.runtime.RuntimeSyncService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

data class ChainService(
    val runtimeProvider: RuntimeProvider,
    val connection: ChainConnection
)

class ChainRegistry @Inject constructor(
    private val runtimeProviderPool: RuntimeProviderPool,
    private val connectionPool: ConnectionPool,
    private val runtimeSubscriptionPool: RuntimeSubscriptionPool,
    private val chainDao: ChainDao,
    private val chainSyncService: ChainSyncService,
    private val runtimeSyncService: RuntimeSyncService,
    private val updatesMixin: UpdatesMixin
) : CoroutineScope by CoroutineScope(Dispatchers.Default), UpdatesProviderUi by updatesMixin {

    val currentChains = chainDao.joinChainInfoFlow()
        .mapList(::mapChainLocalToChain)
        .diffed()
        .map { (removed, addedOrModified, all) ->
            removed.forEach {
                val chainId = it.id

                runtimeProviderPool.removeRuntimeProvider(chainId)
                runtimeSubscriptionPool.removeSubscription(chainId)
                runtimeSyncService.unregisterChain(chainId)
                connectionPool.removeConnection(chainId)
            }

            updatesMixin.startChainsSyncUp(addedOrModified.filter { !it.nodes.isNullOrEmpty() }.map { it.id })
            addedOrModified.filter { !it.nodes.isNullOrEmpty() }.forEach { chain ->

                val connection = connectionPool.setupConnection(
                    chain,
                    onSelectedNodeChange = { chainId, newNodeUrl ->
                        launch { selectNode(NodeId(chainId to newNodeUrl)) }
                    }
                )

                runtimeProviderPool.setupRuntimeProvider(chain)
                runtimeSyncService.registerChain(chain, connection)
                runtimeSubscriptionPool.setupRuntimeSubscription(chain, connection)
            }

            all
        }
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()
        .inBackground()
        .shareIn(this, SharingStarted.Eagerly, replay = 1)

    val chainsById = currentChains.map { chains -> chains.associateBy { it.id } }
        .inBackground()
        .shareIn(this, SharingStarted.Eagerly, replay = 1)

    init {
        syncUp()
    }

    fun syncUp() {
        launch {
            runCatching { chainSyncService.syncUp() }
        }
    }

    fun getConnection(chainId: String) = connectionPool.getConnection(chainId)
    fun getConnectionOrNull(chainId: String) = connectionPool.getConnectionOrNull(chainId)

    fun getRuntimeProvider(chainId: String): RuntimeProvider {
        return runtimeProviderPool.getRuntimeProvider(chainId)
    }

    fun getAsset(chainId: ChainId, chainAssetId: String) = chainsById.replayCache.lastOrNull()?.get(chainId)?.assets?.firstOrNull {
        it.id == chainAssetId
    }

    suspend fun getChain(chainId: ChainId) = chainsById.first().getValue(chainId)

    fun nodesFlow(chainId: String) = chainDao.nodesFlow(chainId)
        .mapList(::mapNodeLocalToNode)

    suspend fun selectNode(id: NodeId) = chainDao.selectNode(id.chainId, id.nodeUrl)

    suspend fun addNode(chainId: ChainId, nodeName: String, nodeUrl: String) =
        chainDao.insertChainNode(ChainNodeLocal(chainId, nodeUrl, nodeName, isActive = false, isDefault = false))

    suspend fun deleteNode(id: NodeId) = chainDao.deleteNode(id.chainId, id.nodeUrl)

    suspend fun getNode(id: NodeId) = mapNodeLocalToNode(chainDao.getNode(id.chainId, id.nodeUrl))

    suspend fun updateNode(id: NodeId, name: String, url: String) = chainDao.updateNode(id.chainId, id.nodeUrl, name, url)
}

suspend fun ChainRegistry.chainWithAsset(chainId: ChainId, assetId: String): Pair<Chain, Chain.Asset> {
    val chain = chainsById.first().getValue(chainId)

    return chain to chain.assetsById.getValue(assetId)
}

suspend fun ChainRegistry.getRuntime(chainId: ChainId): RuntimeSnapshot {
    return getRuntimeProvider(chainId).get()
}

fun ChainRegistry.getSocket(chainId: ChainId) = getConnection(chainId).socketService
fun ChainRegistry.getSocketOrNull(chainId: ChainId) = getConnectionOrNull(chainId)?.socketService

fun ChainRegistry.getService(chainId: ChainId) = ChainService(
    runtimeProvider = getRuntimeProvider(chainId),
    connection = getConnection(chainId)
)
