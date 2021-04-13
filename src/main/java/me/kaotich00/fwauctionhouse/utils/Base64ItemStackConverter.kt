package me.kaotich00.fwauctionhouse.utils

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

object Base64ItemStackConverter {

    fun toBase64(stack: ItemStack): String = try {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            it.writeObject(stack)
        }
        Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("Unable to save item stack.", e)
    }

    fun fromBase64(data: String): ItemStack = try {
        BukkitObjectInputStream(
            ByteArrayInputStream(Base64Coder.decodeLines(data))
        ).use {
            it.readObject() as ItemStack
        }
    } catch (e: ClassNotFoundException) {
        throw IOException("Unable to decode class type.", e)
    }
}