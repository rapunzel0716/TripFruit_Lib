/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uuu.tf.entity;

/**
 *
 * @author Rapunzel_PC
 */
public class TFException extends Exception {

    /**
     * Creates a new instance of <code>TFException</code> without detail
     * message.
     */
    public TFException() {
    }

    /**
     * Constructs an instance of <code>TFException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public TFException(String msg) {
        super(msg);
    }
    public TFException(String message, Throwable cause) {
        super(message, cause);
    }
}
