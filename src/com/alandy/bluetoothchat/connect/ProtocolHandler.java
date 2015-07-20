package com.alandy.bluetoothchat.connect;

/**
 * Processing network protocol, the data packets or unpack
 * @author Fangjun
 *
 * @param <T>
 */
public interface ProtocolHandler <T> {
	public byte[] encodePackage(T data);
	public T decodePackage(byte[] netData);
}
