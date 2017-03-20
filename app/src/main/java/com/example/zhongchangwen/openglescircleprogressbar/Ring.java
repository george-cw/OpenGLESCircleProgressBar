package com.example.zhongchangwen.openglescircleprogressbar;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zhongchangwen on 2017/3/17.
 */

public class Ring {
    private FloatBuffer vertexBuffer;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position =  uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    private final int mProgram;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    private int vertexCount = 360 * 2;
    private float radius = 1.0f;
    // Outer vertices of the circle
    private int outerVertexCount = vertexCount / 2 - 1;

    static final int COORDS_PER_VERTEX = 3;
    private float ringCoords[] = new float[vertexCount * COORDS_PER_VERTEX]; // (x,y,z) for each vertex
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    ByteBuffer bb = ByteBuffer.allocateDirect(ringCoords.length * 4);
    private float mProgressArc = 1 / 6.0f;
    private boolean positive = true;
    private float position = 0.0f;
    private int mRepeat = 0;
    private float mRadius = 0.5f;

    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    public Ring() {

        bb.order(ByteOrder.nativeOrder());

        generateVertex(mProgressArc);

        // create empty OpenGL ES Program
        mProgram = Utils.initProgram(vertexShaderCode, fragmentShaderCode);
    }

    private void generateVertex(float progressArc) {
        float center_x = 0.0f;
        float center_y = 0.0f;
        int idx = 0;

        if (positive) {
            for (int i = 0; i < outerVertexCount; ++i) {
                float percent = (i / (float) (outerVertexCount - 1));
                float rad = (float) (percent * Math.PI * progressArc) + position;

                //Vertex position
                float outer_x = (center_x + radius * (float) Math.cos(rad)) * mRadius;
                float outer_y = (center_y + radius * (float) Math.sin(rad)) * mRadius;

                ringCoords[idx++] = outer_x;
                ringCoords[idx++] = outer_y;
                ringCoords[idx++] = 3.0f;

                ringCoords[idx++] = outer_x;
                ringCoords[idx++] = outer_y;
                ringCoords[idx++] = 5.0f;
            }
        } else {
            for (int i = 0; i < outerVertexCount; ++i) {
                float percent = (i / (float) (outerVertexCount - 1));
                float rad = (float) (percent * Math.PI * progressArc) + (11 / 6.0f - mProgressArc) * (float) Math.PI + position;

                //Vertex position
                float outer_x = (center_x + radius * (float) Math.cos(rad))*0.5f;
                float outer_y = (center_y + radius * (float) Math.sin(rad))*0.5f;

                ringCoords[idx++] = outer_x;
                ringCoords[idx++] = outer_y;
                ringCoords[idx++] = 3.0f;

                ringCoords[idx++] = outer_x;
                ringCoords[idx++] = outer_y;
                ringCoords[idx++] = 5.0f;
            }

        }

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(ringCoords);
        vertexBuffer.position(0);

        if (positive) {
            mProgressArc += 0.01f;
        } else {
            mProgressArc -= 0.01f;
        }

        if (mProgressArc >= 11 / 6.0f) {
            positive = false;
        }
        if (mProgressArc <= 1 / 6.0f) {
            positive = true;
            mRepeat++;
            position = -1 / 3.0f * (float) Math.PI * mRepeat;
            if (mRepeat >= 6)
                mRepeat = 0;
        }
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        //refresh the ring's state
        generateVertex(mProgressArc);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the ring
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}