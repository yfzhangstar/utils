package com.cellsgame.mq.zmq;

import com.cellsgame.common.util.cmd.ConsoleCmdRunner;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * broker独立启动器
 * @author peterveron
 *
 */
public class BrokerBootstrap {


	private static BrokerBootstrap bootstrap = new BrokerBootstrap();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private ZMQBroker b = new ZMQBroker();

	public static void main(String[] args) {
		System.out.println("start zmq broker id["+args[0]+"] at address:"+args[1]);
		Runtime r = Runtime.getRuntime();
		bootstrap.b.start(r.availableProcessors(), 1000, args[0], args[1]);
		bootstrap.running.set(true);
		ConsoleCmdRunner runner = new ConsoleCmdRunner(bootstrap.running, "", bootstrap::stop);
		runner.run();
		System.out.println("zmq broker start end");
	}

	private void stop(){
		try {
			b.stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
