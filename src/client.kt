import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress

fun main(args: Array<String>) {
    val s = DatagramSocket()
    val data = "Hello".toByteArray(Charsets.UTF_8)
    val dp = DatagramPacket(data, data.size)
    dp.port = 50001
    dp.address = InetAddress.getLoopbackAddress()
    s.send(dp)
    val recv = ByteArray(4096)
    val d = DatagramPacket(recv, recv.size)
    s.receive(d)

    println("${d.address}:${d.port} ${d.length} ${d.offset} ${String(d.data, d.offset, d.length)}")
//        val b = "ok".toByteArray(Charsets.UTF_8)
//        val pack = DatagramPacket(b, b.size)
//        pack.address = dp.address
//        pack.port = dp.port
//        s.send(pack)
}