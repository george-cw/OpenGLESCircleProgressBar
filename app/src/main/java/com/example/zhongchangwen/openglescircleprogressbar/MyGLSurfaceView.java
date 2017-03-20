package com.example.zhongchangwen.openglescircleprogressbar;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by zhongchangwen on 2017/3/17.
 */

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        setRenderer(mRenderer);
    }
}
