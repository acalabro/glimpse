package it.cnr.isti.labsedc.glimpse.event;

public class GlimpseBaseEventMachineInformation<T> extends GlimpseBaseEventGeneric<String> {

	private static final long serialVersionUID = 4025157539789516228L;

	private long CPU_PERCENTAGE;
	private long FREE_RAM_MB;
	private long TOTAL_RAM_MB;
	private long FREE_DISK_MB;
	private long TOTAL_DISK_MB; 
	private long UP_STREAM_KBPS; 
	private long DOWN_STREAM_KBPS;
	
	public GlimpseBaseEventMachineInformation(
			String osInfo, String probeID, Long timeStamp, String eventName, boolean isException, String extraDataField, 
			String cpuInfo, 
			long CpuPercentage,
			long FreeRamMb,
			long TotalRamMb,
			long FreeDiskMb,
			long TotalDiskMb, 
			long UpStreamKbps, 
			long DownStreamKbps) {
		super(osInfo, probeID, timeStamp, eventName, isException, extraDataField);
		this.CPU_PERCENTAGE = CpuPercentage;
		this.FREE_RAM_MB = FreeRamMb;
		this.TOTAL_RAM_MB = TotalRamMb;
		this.FREE_DISK_MB = FreeDiskMb;
		this.TOTAL_DISK_MB = TotalDiskMb;
		this.UP_STREAM_KBPS = UpStreamKbps;
		this.DOWN_STREAM_KBPS = DownStreamKbps;
	}

	long getCpu_Percentage() {
		return CPU_PERCENTAGE;
	}

	long getFree_Ram_MB() {
		return FREE_RAM_MB;
	}

	long getTotal_Ram_MB() {
		return TOTAL_RAM_MB;
	}

	long getFree_Disk_MB() {
		return FREE_DISK_MB;
	}

	long getTotal_Disk_MB() {
		return TOTAL_DISK_MB;
	}

	long getUp_Stream_Kbps() {
		return UP_STREAM_KBPS;
	}

	long getDown_Stream_Kbps() {
		return DOWN_STREAM_KBPS;
	}

	void setCpu_Percentage(long cPU_PERCENTAGE) {
		CPU_PERCENTAGE = cPU_PERCENTAGE;
	}

	void setFree_Ram_MB(long fREE_RAM_MB) {
		FREE_RAM_MB = fREE_RAM_MB;
	}

	void setTotal_Ram_MB(long tOTAL_RAM_MB) {
		TOTAL_RAM_MB = tOTAL_RAM_MB;
	}

	void setFree_Disk_MB(long fREE_DISK_MB) {
		FREE_DISK_MB = fREE_DISK_MB;
	}

	void setTotal_Disk_MB(long tOTAL_DISK_MB) {
		TOTAL_DISK_MB = tOTAL_DISK_MB;
	}

	void setUp_Stream_Kbps(long uP_STREAM_KBPS) {
		UP_STREAM_KBPS = uP_STREAM_KBPS;
	}

	void setDown_Stream_Kbps(long dOWN_STREAM_KBPS) {
		DOWN_STREAM_KBPS = dOWN_STREAM_KBPS;
	}
}
