package me.kaotich00.fwauctionhouse.utils

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

object SerializationUtil {
    @Throws(IllegalStateException::class)
    fun toBase64(stack: ItemStack?): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeObject(stack)

            // Serialize that array
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stack.", e)
        }
    }

    @Throws(IOException::class)
    fun fromBase64(data: String?): ItemStack {
        return try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            try {
                dataInput.readObject() as ItemStack
            } finally {
                dataInput.close()
            }
        } catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }
}