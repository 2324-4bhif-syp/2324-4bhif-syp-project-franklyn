import {environment} from "../../../../env/environment";

export class Timer {
  constructor() {
    this.screenshotCaptureIntervalProp = 10;
    this.patrolSpeedProp = environment.patrolSpeed;
    this.nextClientScheduleTimeProp = environment.nextClientScheduleTime;
    this.reloadDashboardIntervalProp = environment.reloadDashboardInterval;
  }

  private screenshotCaptureIntervalProp: number;
  private examineeTimerIdProp: number | undefined;

  private patrolSpeedProp: number;
  private patrolScheduleTimerProp: number | undefined;

  private nextClientScheduleTimeProp: number;
  private clientScheduleTimerIdProp: number | undefined;

  private reloadDashboardIntervalProp: number;
  private serverMetricsTimerIdProp: any | undefined

  //region <unformatted time-getter and setter>
  get screenshotCaptureInterval() {
    return this.screenshotCaptureIntervalProp;
  }

  get patrolSpeed() {
    return this.patrolSpeedProp;
  }

  get nextClientTime() {
    return this.nextClientScheduleTimeProp;
  }

  get reloadDashboardInterval() {
    return this.reloadDashboardIntervalProp;
  }

  set screenshotCaptureInterval(val) {
    this.screenshotCaptureIntervalProp = val;
  }

  set patrolSpeed(val) {
    this.patrolSpeedProp = val;
  }

  set nextClientTime(val) {
    this.nextClientScheduleTimeProp = val;
  }

  set reloadDashboardInterval(val) {
    this.reloadDashboardIntervalProp = val;
  }
  //endregion

  //region <formatted time-getter>
  get screenshotCaptureIntervalMilliseconds() {
    return this.screenshotCaptureIntervalProp*1000;
  }

  get patrolSpeedMilliseconds() {
    return this.patrolSpeedProp*1000;
  }

  get nextClientTimeMilliseconds() {
    return this.nextClientScheduleTimeProp*1000;
  }

  get reloadDashboardIntervalMilliseconds() {
    return this.reloadDashboardIntervalProp*1000;
  }
  //endregion

  //region <timer getter and setter>
  get examineeTimerId() {
    return this.examineeTimerIdProp;
  }

  get patrolScheduleTimer() {
    return this.patrolScheduleTimerProp;
  }

  get clientScheduleTimerId() {
    return this.clientScheduleTimerIdProp;
  }

  get serverMetricsTimerId() {
    return this.serverMetricsTimerIdProp;
  }

  set examineeTimerId(val) {
    this.examineeTimerIdProp = val;
  }

  set patrolScheduleTimer(val) {
    this.patrolScheduleTimerProp = val;
  }

  set clientScheduleTimerId(val) {
    this.clientScheduleTimerIdProp = val;
  }

  set serverMetricsTimerId(val) {
    this.serverMetricsTimerIdProp = val;
  }
  //endregion
}
