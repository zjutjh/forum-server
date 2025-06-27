package org.jh.forum.start.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.Blake3;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author SugarMGP
 */
public class BlakeUtils {
    /**
     * 计算 MultipartFile 的 BLAKE3 哈希值
     *
     * @param file 上传的文件
     * @return 十六进制格式的哈希字符串
     * @throws IOException 文件读取异常
     */
    public static String computeHash(MultipartFile file) throws IOException {
        Blake3 hasher = Blake3.initHash();

        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                hasher.update(buffer, 0, bytesRead);
            }
        }

        // 获取哈希值
        byte[] hashBytes = new byte[128];
        hasher.doFinalize(hashBytes);

        // 返回十六进制字符串
        return new String(Hex.encodeHex(hashBytes, true));
    }
}
