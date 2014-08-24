package com.example.glhorizontalview;

public abstract class ModelChangeCallback {
		public abstract void onModelChanged(ModelState stat);
		
		protected ModelState mStat;
		public void setState(ModelState state) {
			mStat = state;
		}
		
		public static class ModelState {
				int lastIndex;
				float lastOffset;
				int lastRenderMode;
				public ModelState(int idx, float offset, int rendermode) {
					lastIndex = idx;
					lastOffset = offset;
					lastRenderMode = rendermode;
				}
				public void setState(ModelState another) {
					lastIndex = another.lastIndex;
					lastOffset = another.lastOffset;
					lastRenderMode = another.lastRenderMode;
				}
		}
}
