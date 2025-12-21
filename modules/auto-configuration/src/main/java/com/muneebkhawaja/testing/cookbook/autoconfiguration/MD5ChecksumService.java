package com.muneebkhawaja.testing.cookbook.autoconfiguration;

import org.springframework.util.DigestUtils;

import java.util.Objects;

final class MD5ChecksumService implements ChecksumService {

    @Override
    public String calculateChecksum(byte[] input) {
        Objects.requireNonNull(input, "Input cannot be null");
        return DigestUtils.md5DigestAsHex(input);
    }
}