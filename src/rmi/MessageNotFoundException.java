/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;
public class MessageNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	private int[] msgNum = null;

	public int[] getMsgNum() {
		return msgNum;
	}

	public void setMsgNum(int[] msgNum) {
		this.msgNum = msgNum;
	}

}
