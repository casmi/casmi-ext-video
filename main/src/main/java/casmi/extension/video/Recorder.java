/*
 *   casmi-ext-video
 *   https://github.com/casmi/casmi-ext-video
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

import com.jogamp.opengl.util.awt.Screenshot;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 * Capture sequential images from casmi Applet and export as a movie.
 * <p>
 * This extension uses <a href=http://www.xuggle.com/xuggler>Xuggler</a> video
 * library.  
 * 
 * @author T. Takeuchi
 */
public class Recorder implements Updatable {

    private final Applet applet;
    
    private IMediaWriter mediaWriter;
    private boolean isRecording = false;
    private boolean recordInBackground = true;
    private long recordTime = 0;
    private long recordSpan = 0;
    
    public Recorder(Applet applet) {
        this.applet = applet;
    }
    
    @Override
    public void update() {
        if (isRecording) {
            BufferedImage bi = Screenshot.readToBufferedImage(applet.getWidth(), applet.getHeight(), !recordInBackground);
            if (recordTime <= 0) {
                recordTime = System.currentTimeMillis();
            }
            long elapse = System.currentTimeMillis() - recordTime;
            if (0 < recordSpan && recordSpan < elapse) {
                stop();
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
    public void start(String file) {
        start(file, true, 0);
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
    public void start(String file, boolean background, int sec) {
        start(file, background, sec, Codec.getDefaultCodec());
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
     *            call {@link #stop()}.
     * @param codec
     *            a codec of a movie.
     */
    public void start(String file, boolean background, int sec, Codec codec) {
        stop();

        mediaWriter = ToolFactory.makeWriter(file);
        mediaWriter.addVideoStream(0, 0, Codec.toXugglerCodec(codec), 
                                   applet.getWidth(), applet.getHeight());
        recordInBackground = background;
        recordSpan = sec * 1000;
        recordTime = 0;
        isRecording = true;
        
        applet.addUpdateObject(this);
    }
    
    /**
     * @param file
     * @deprecated
     */
    public void record(String file) {
        start(file, true, 0);
    }
    
    /**
     * @param file
     * @param background
     * @param sec
     * @deprecated
     */
    public void record(String file, boolean background, int sec) {
        start(file, background, sec, Codec.getDefaultCodec());
    }
    
    /**
     * @param file
     * @param background
     * @param sec
     * @param codec
     * @deprecated
     */
    public void record(String file, boolean background, int sec, Codec codec) {
        start(file, background, sec, codec);
    }
    
    /**
     * Stops recording.
     */
    public void stop() {
        isRecording = false;
        applet.removeUpdateObject(this);
        if (mediaWriter != null && mediaWriter.isOpen()) {
            mediaWriter.close();
        }
    }
    
    /**
     * Stops recording.
     * @deprecated
     */
    public void stopRecord() {
        stop();
    }

    public boolean isRecording() {
        return isRecording;
    }
}
