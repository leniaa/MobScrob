/**
 * ID3v24Body.java
 * 
 * This program is distributed under the terms of the GNU General Public 
 * License
 * Copyright 2008 NJ Pearman
 *
 * This file is part of MobScrob.
 *
 * MobScrob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobScrob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MobScrob.  If not, see <http://www.gnu.org/licenses/>.
 */
package mobscrob.id3;

import java.io.IOException;

import mobscrob.mp3.MP3Stream;

/**
 * @author Neill
 * 
 */
public class ID3v24Body extends AbstractID3Body {
	private static final int ID324_FRAME_HEADER_LEN = 10;

//	private static final int FLAG_GROUP_IDENTIFIER = 64;
//	private static final int FLAG_COMPRESSION = 8;
//	private static final int FLAG_ENCRYPTION = 4;
//	private static final int FLAG_UNSYNCHRONIZATION = 2;
//	private static final int FLAG_DATA_LENGTH_INDICATOR = 1;

	char[] currentId;

	public ID3v24Body(ID3Header header, MP3Stream is) {
		super(header, is);
	}

	private byte[] seekNextFrame() throws IOException {
		// read first ten bytes from input stream
		byte[] frameH = new byte[ID324_FRAME_HEADER_LEN];

		int byteCount;
		byteCount = is.read(frameH);
		if (byteCount != ID324_FRAME_HEADER_LEN) {
			throw ID3Exception.INCORRECT_FRAME_HEADER_LENGTH;
		}

		currentId = new char[4];
		currentId[0] = (char) frameH[0];
		currentId[1] = (char) frameH[1];
		currentId[2] = (char) frameH[2];
		currentId[3] = (char) frameH[3];

		if (currentId[0] == 0 && currentId[1] == 0 && currentId[2] == 0
				&& currentId[3] == 0) {
			return null;
		}

		return frameH;
	}

	public Frame readNextFrame() throws IOException {

		byte[] frameH = null;

		while ((frameH = seekNextFrame()) == null) {
			if (readComplete()) {
				return null;
			}
		}

		int frameLen = SynchsafeInteger.valueOf(new byte[] { frameH[4],
				frameH[5], frameH[6], frameH[7] });

		return new Frame(new String(currentId), frameLen,
				readRawFrameBytes(frameLen));
	}
}
