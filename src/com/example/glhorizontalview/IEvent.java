package com.example.glhorizontalview;

public interface IEvent {
	public void onClick(float x , float y);
	public void onScroll(float offset_x,float offset_y);
	public void onFling(float velocity_x, float velocity_y);
	public void onFinish(float x, float y);
}
