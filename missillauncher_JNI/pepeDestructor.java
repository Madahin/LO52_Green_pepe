package com.android.green_pepe.missillauncher

class PepeDestructor{

	private static native int mlbinInitUsb(void);
	private static native int mlbinFreeUsb(void);
	private static native int mlbinMoveDown(void);
	private static native int mlbinFire(void);
	private static native int mlbinMoveLeft(void);
	private static native int mlbinMoveRight(void);
	private static native int mlbinMoveUp(void);
	private static native int mlbinStop(void);


	public static void main()
	{
		mlbinInitUsb();
	}

	static
	{
		system.loadLibrary("mlbinInitUsb");
	}
}
