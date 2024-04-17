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
  readonly serverMetrics: ServerMetrics
}

const initialState: Model = {
  cacheBuster: {
    cachebustNum: 0
  },
  examineeData: {
    examinees: [],
    getExamineeIntervalSpeed: environment.getExamineeInterval
  },
  patrol: new Patrol(
    environment.getExamineeInterval,
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
  }
};

export const store = new BehaviorSubject<Model>(initialState);

export function set(recipe: (model: Draft<Model>)=>void) {
  const nextState = produce(store.getValue, recipe);
  store.next(nextState);
}
