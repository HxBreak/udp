import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Client(
    val addr:InetAddress,
    val port:Int,
    val time:Long
)

fun InetAddress.toLong():Long{
    val buf = ByteBuffer.allocate(Long.SIZE_BYTES).order(ByteOrder.BIG_ENDIAN)
    buf.put(address)
    buf.position(0)
    return buf.long
}

fun main(args: Array<String>) {
    val s = DatagramSocket(50001)
    val data = ByteArray(4096)
    val dp = DatagramPacket(data, data.size)
    val list = mutableListOf<Client>()
    while (true){
        s.receive(dp)
        if("Hello".equals(String(dp.data, dp.offset, dp.length))){
            val r = list.firstOrNull {
                dp.address.hostAddress.equals( it.addr.hostAddress ) &&
                        it.port == dp.port
            }
            if(r == null){
                list.add(Client(dp.address, dp.port, System.currentTimeMillis()))
            }
            if (list.size > 1){
                val lift = list.size % 2
                val pairCount = (list.size - lift) / 2
                for (i in 0 until pairCount){
                    val c1 = list.get(0)
                    val c2 = list.get(1)
                    val bo = ByteArrayOutputStream()
                    DataOutputStream(bo).apply {
                        writeInt(0xf1f1f1f)
                        writeLong(c2.addr.toLong())
                        writeInt(c2.port)
                    }
                    val out1 = bo.toByteArray()
                    val p1 = DatagramPacket(out1, out1.size)
                    p1.address = c1.addr
                    p1.port = c1.port
                    s.send(p1)
                    val bo2 = ByteArrayOutputStream()
                    DataOutputStream(bo2).apply {
                        writeInt(0xf1f1f1f)
                        writeLong(c1.addr.toLong())
                        writeInt(c1.port)
                    }
                    val out2 = bo2.toByteArray()
                    val p2 = DatagramPacket(out2, out2.size)
                    p2.address = c2.addr
                    p2.port = c2.port
                    s.send(p2)
                    list.removeAt(0)
                    list.removeAt(0)
                }
            }
        }
        println("${dp.address}:${dp.port} ${dp.length} ${dp.offset} ${String(dp.data, dp.offset, dp.length)}")
//        val b = "ok".toByteArray(Charsets.UTF_8)
//        val pack = DatagramPacket(b, b.size)
//        pack.address = dp.address
//        pack.port = dp.port
//        s.send(pack)
    }
}