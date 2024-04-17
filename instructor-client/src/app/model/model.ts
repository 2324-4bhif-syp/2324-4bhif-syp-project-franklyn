import {BehaviorSubject} from "rxjs";
import {Draft, produce} from "immer";
import {CacheBuster} from "./entity/CacheBuster";
import {ExamineeData} from "./entity/ExamineeData";
import {Patrol} from "./entity/Patrol";
import {ServerMetrics} from "./entity/ServerMetrics";
import {environment} from "../../../env/environment";

export interface Model {
  readonly cacheBuster: CacheBuster,
  readonly examineeData: ExamineeData,
  readonly patrol: Patrol,
  readonly serverMetrics: ServerMetrics,
  readonly resetText: string
}

const initialState: Model = {
  cacheBuster: {
    cachebustNum: 0
  },
  examineeData: {
    examinees: [],
    screenshotCaptureInterval: environment.screenshotCaptureInterval
  },
  patrol: new Patrol(
    environment.nextClientScheduleTime,
    environment.patrolSpeed,
    false
  ),
  serverMetrics: {
    cpuUsagePercent: 0,
    totalDiskSpaceInBytes: 0,
    remainingDiskSpaceInBytes: 0,
    savedScreenshotsSizeInBytes: 0,
    maxAvailableMemoryInBytes: 0,
    totalUsedMemoryInBytes: 0,
    timerId: undefined
  },
  resetText: ""
};

export const store = new BehaviorSubject<Model>(initialState);

export function set(recipe: (model: Draft<Model>)=>void) {
  const nextState = produce(store.value, recipe);
  store.next(nextState);
}
