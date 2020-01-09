package com.utsman.kemana.backend

import com.corundumstudio.socketio.SocketIOServer
import java.net.ServerSocket

interface SocketInstance {
    fun getSocketServer(action: (SocketIOServer?) -> Unit)
}