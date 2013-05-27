/*
 *   casmi-ext-video
 *   https://github.com/casmi/casmi-ext-video
 *   Copyright (C) 2012, Xcoo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package casmi.extension.video;

import casmi.Applet;
import casmi.AppletRunner;
import casmi.KeyEvent;
import casmi.MouseButton;
import casmi.MouseEvent;
import casmi.graphics.color.ColorSet;
import casmi.graphics.color.RGBColor;
import casmi.graphics.element.Circle;
import casmi.graphics.element.Element;
import casmi.graphics.element.MouseClickCallback;
import casmi.graphics.element.MouseOverCallback;
import casmi.tween.Tween;
import casmi.tween.TweenElement;
import casmi.tween.TweenType;
import casmi.tween.equations.Sine;
import casmi.util.SystemUtil;

/**
 * Recording example.
 * <p>
 * Records a window as a MPEG4 movie.
 *
 * @author T. Takeuchi
 *
 * @see casmi.extension.video.Recorder
 */
public class RecordExample extends Applet {

    static final String RECORD_FILE = SystemUtil.JAVA_TMP_PATH + "casmi_record.mp4";

    Recorder recorder;

    Circle circle = new Circle(320, 240, 15);
    Circle button = new Circle(610, 450, 15);

    TweenElement te = new TweenElement(circle);

    @Override
    public void setup() {
        recorder = new Recorder(this);

        setSize(640, 480);

        circle.setCenterColor(ColorSet.YELLOW);
        circle.setEdgeColor(new RGBColor(0.0, 0.0));
        addObject(circle);

        button.setFillColor(new RGBColor(ColorSet.RED, 0.5));

        button.addMouseEventCallback(new MouseOverCallback() {
            @Override
            public void run(MouseOverTypes eventtype, Element element) {
                switch (eventtype) {
                case ENTERED:
                case EXISTED:
                    button.setStroke(true);
                    button.setStrokeColor(ColorSet.WHITE);
                    break;
                case EXITED:
                    button.setStroke(false);
                    break;
                }
            }
        });

        button.addMouseEventCallback(new MouseClickCallback() {
            @Override
            public void run(MouseClickTypes eventtype, Element element) {
                if (eventtype == MouseClickTypes.CLICKED) {
                    if (!recorder.isRecording()) {
                        recorder.start(RECORD_FILE);
                        button.setFillColor(new RGBColor(ColorSet.RED));
                        System.out.println("start recording");
                    } else {
                        recorder.stop();
                        button.setFillColor(new RGBColor(ColorSet.RED, 0.5));
                        System.out.println("stop recording");
                        System.out.println("file: " + RECORD_FILE);
                    }
                }
            }
        });

        addObject(button);
    }

    @Override
    public void update() {}

    @Override
    public void mouseEvent(MouseEvent e, MouseButton b) {
        if (e == MouseEvent.MOVED) {
        	addTween(Tween.to(te, TweenType.POSITION, 500, Sine.OUT).target(getMouseX(), getMouseY()));
        }
    }

    @Override
    public void keyEvent(KeyEvent e) {}

    public static void main(String[] args) {
        AppletRunner.run("casmi.extension.video.RecordExample", "Screen Recording Example");
    }
}
