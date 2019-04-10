package com.xyz.browser.dm.util;


import java.net.URI;

import java.util.Map;

import java.util.Timer;

import org.java_websocket.client.WebSocketClient;

import org.java_websocket.drafts.Draft;

import org.java_websocket.handshake.ServerHandshake;

public abstract class ReconnectingWebSocketClient extends WebSocketClient {

    private boolean debug = true;

    private Integer reconnectInterval = 1000;

    private Integer maxReconnectInterval = 30000;

    private Double reconnectDecay = 1.5;

    private Integer reconnectAttempts = 0;

    private Integer maxReconnectAttempts = 30;

    private Boolean forcedClose = false;

    private Timer reconnectTimer;

    private volatile Boolean isReconnecting = false;

    private ReschedulableTimerTask reconnectTimerTask;

    public ReconnectingWebSocketClient( URI serverUri , Draft protocolDraft ) {

        super( serverUri, protocolDraft, null, 0 );

    }

    public ReconnectingWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders,

                                       int connectTimeout) {

        super(serverUri, protocolDraft, httpHeaders, connectTimeout);

// TODO Auto-generated constructor stub

    }

    @Override

    public void onClose(int arg0, String arg1, boolean arg2) {

        if (forcedClose) {

            cancelReconnectionTimer();
// 调用close 方法
            close(arg0, arg1, arg2);

        } else {

            if (!isReconnecting) {

                restartReconnectionTimer();

            }

            isReconnecting = true;

        }

    }

    @Override

    public void onError(Exception exception) {

        error(exception);

    }

    @Override

    public void onMessage(String message) {

        message(message);

    }

    @Override

    public void onOpen(ServerHandshake arg0) {

        open(arg0);

    }

    private void restartReconnectionTimer() {

        cancelReconnectionTimer();

        reconnectTimer = new Timer("reconnectTimer");

        reconnectTimerTask = new ReschedulableTimerTask() {

            @Override

            public void run() {

                if (reconnectAttempts >= maxReconnectAttempts) {

                    cancelReconnectionTimer();

                    if (debug) {

                        System.out.println("以达到最大重试次数:" + maxReconnectAttempts + "，已停止重试!!!!");

                    }
                    return;

                }

                reconnectAttempts++;

                try {

                    Boolean isOpen = reconnectBlocking();

                    if (isOpen) {

                        if (debug) {

                            System.out.println("连接成功，重试次数为:" + reconnectAttempts);

                        }

                        cancelReconnectionTimer();

                        reconnectAttempts = 0;

                        isReconnecting = false;

                    } else {

                        if (debug) {

                            System.out.println("连接失败，重试次数为:" + reconnectAttempts);

                        }

                        double timeoutd = reconnectInterval * Math.pow(reconnectDecay, reconnectAttempts);

                        int timeout = Integer.parseInt(new java.text.DecimalFormat("0").format(timeoutd));

                        timeout = timeout > maxReconnectInterval ? maxReconnectInterval : timeout;

                        System.out.println(timeout);

                        reconnectTimerTask.re_schedule2(timeout);

                    }

                } catch (InterruptedException e) {

                    e.printStackTrace();

                }

            }

        };

        reconnectTimerTask.schedule(reconnectTimer,reconnectInterval);

    }

    private void cancelReconnectionTimer() {

        if (reconnectTimer != null) {

            reconnectTimer.cancel();

            reconnectTimer = null;

        }

        if (reconnectTimerTask != null) {

            reconnectTimerTask.cancel();

            reconnectTimerTask = null;

        }

    }

// ABTRACT METHODS /////////////////////////////////////////////////////////

    public abstract void open(ServerHandshake handshakedata);

    public abstract void message(String message);

    public abstract void close(int code, String reason, boolean remote);

    public abstract void error(Exception ex);

    public void setForcedClose(Boolean forcedClose) {
        this.forcedClose = forcedClose;
    }
}