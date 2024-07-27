// IMusic.aidl
package com.example.center;

// Declare any non-default types here with import statements

interface IMusic {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void play();
    void pause();
    void resume();
    void stop();
    void release();

}