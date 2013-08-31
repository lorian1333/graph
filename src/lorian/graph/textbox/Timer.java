package lorian.graph.textbox;

import java.util.ArrayList;
import java.util.List;

public class Timer{
	private List<TimerTickListener> listeners;
	private int interval;
	Thread thread;
	public Timer()
	{
		setInterval(100);
		listeners = new ArrayList<TimerTickListener>();
		
	}
	public Timer(int interval)
	{
		setInterval(interval);
		listeners = new ArrayList<TimerTickListener>();
		final int interval_f = (int) (interval * Math.pow(10, 6));
		thread = new Thread()
		{
			public void run()
			{
				long count=0;
				long start = System.nanoTime();
				while(this.isAlive())
				{
					count = System.nanoTime() - start;
					if(count >= (interval_f))
					{
						invokeListeners();
						start = System.nanoTime();
					}
				}
			}
		};
	}
	private void invokeListeners()
	{
		for(TimerTickListener l : listeners)
		{
			l.onTick();
		}
	
	}
	public void start()
	{
		thread.start();
		
	}
	public void stop()
	{
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void reset()
	{
		stop();
		start();
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public List<TimerTickListener> getListeners() {
		return listeners;
	}
	
	public void addTickListener(TimerTickListener listener)
	{
		listeners.add(listener);
	}
}
