package com.lyl.study.trainning.engine.core.net;

import com.lyl.study.trainning.engine.core.rpc.dispatch.Dispatcher;
import com.lyl.study.trainning.engine.core.rpc.serialize.Codec;
import com.sun.xml.internal.ws.util.CompletedFuture;
import lombok.Getter;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 客户端和服务器公共基类
 *
 * @param <IN> 受理的消息类型
 * @author liyilin
 */
public abstract class NetworkEndpoint<IN> {
    @Getter
    private final Dispatcher dispatcher;
    @Getter
    private final Codec<IN, byte[]> codec;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public NetworkEndpoint(Dispatcher dispatcher, Codec<IN, byte[]> codec) {
        this.dispatcher = dispatcher;
        this.codec = codec;
    }

    /**
     * 开启连接
     */
    public final Future<Void> start() {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Peer already started");
        }

        return doStart();
    }

    /**
     * 实际执行启动逻辑的方法
     *
     * @return 异步执行Future
     */
    protected abstract Future<Void> doStart();

    /**
     * 关闭连接
     */
    public final Future<Void> shutdown() {
        if (started.compareAndSet(true, false)) {
            return doShutdown();
        }
        return new CompletedFuture<>(null, null);
    }

    /**
     * 实际执行停止逻辑的方法
     *
     * @return 异步执行Future
     */
    protected abstract Future<Void> doShutdown();

    /**
     * 判断终端点是否处于开启状态，初始化完毕后将会变为{@link NetworkEndpoint#isActive() 生效状态}
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * 终端点是否处于生效状态
     */
    public abstract boolean isActive();
}
