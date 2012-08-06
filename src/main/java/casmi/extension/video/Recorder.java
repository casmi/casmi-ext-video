/*
 *   casmi-ext-video
 *   http://casmi.github.com/
 *   Copyright (C) 2012, Xcoo, Inc.
 *
 *  casmi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package casmi.extension.video;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import casmi.Applet;
import casmi.Updatable;
import casmi.util.DateUtil;

import com.jogamp.opengl.util.awt.Screenshot;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 * 
 * @author T. Takeuchi
 *
 */
public class Recorder implements Updatable {

    private final Applet applet;
    
    private IMediaWriter mediaWriter;
    private boolean recordFlag = false;
    private boolean recordBackground = true;
    private int recordTime = 0;
    private int recordSpan = 0;
    
    public Recorder(Applet applet) {
        this.applet = applet;
    }
    
    @Override
    public void update() {
        if (recordFlag) {
            BufferedImage bi = Screenshot.readToBufferedImage(applet.getWidth(), applet.getHeight(), !recordBackground);
            if (recordTime <= 0) {
                recordTime = DateUtil.millis();
            }
            int elapse = DateUtil.millis() - recordTime;
            if (0 < recordSpan && recordSpan < elapse) {
                stopRecord();
            }
            mediaWriter.encodeVideo(0, bi, elapse, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * Records the window as a movie file with background by H264 codec.
     * 
     * @param file
     *            an output file.
     */
    public void record(String file) {
        record(file, true, 0);
    }
    
    /**
     * Records the window as a movie file by H264 codec.
     * 
     * @param file
     *            an output file.
     * @param background
     *            if true, records with background.
     * @param sec
     *            records for specified seconds. if specifies 0, records until
     *            call {@link #stopRecord()}.
     */
    public void record(String file, boolean background, int sec) {
        record(file, background, sec, Codec.getDefaultCodec());
    }
    
    /**
     * Records the window as a movie file.
     * 
     * @param file
     *            an output file.
     * @param background
     *            if true, records with background.
     * @param sec
     *            records for specified seconds. if specifies 0, records until
     *            call {@link #stopRecord()}.
     * @param codec
     *            a codec of a movie.
     */
    public void record(String file, boolean background, int sec, Codec codec) {
        stopRecord();

        mediaWriter = ToolFactory.makeWriter(file);
        mediaWriter.addVideoStream(0, 0, Codec.toXugglerCodec(codec), 
                                   applet.getWidth(), applet.getHeight());
        recordBackground = background;
        recordSpan = sec * 1000;
        recordTime = 0;
        recordFlag = true;
    }
    
    /**
     * Stops recording.
     */
    public void stopRecord() {
        recordFlag = false;
        if (mediaWriter != null) {
            mediaWriter.close();
        }
    }
}
