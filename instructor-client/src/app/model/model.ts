import {BehaviorSubject} from "rxjs";
import {Draft, produce} from "immer";
import {CacheBuster} from "./entity/CacheBuster";
import {ExamineeData} from "./entity/ExamineeData";
import {Patrol} from "./entity/Patrol";
import {ServerMetrics} from "./entity/ServerMetrics";
import {Timer} from "./entity/Timer";

export interface Model {
  readonly cacheBuster: CacheBuster,
  readonly examineeData: ExamineeData,
  readonly patrol: Patrol,
  readonly serverMetrics: ServerMetrics,
  readonly timer: Timer,
  readonly resetText: string
}

const initialState: Model = {
  cacheBuster: {
    cachebustNum: 0
  },
  examineeData: {
    examinees: []
  },
  patrol: {
    isPatrolModeOn: false,
    patrolExaminee: undefined
  },
  serverMetrics: {
    cpuUsagePercent: 0,
    totalDiskSpaceInBytes: 0,
    remainingDiskSpaceInBytes: 0,
    savedScreenshotsSizeInBytes: 0,
    maxAvailableMemoryInBytes: 0,
    totalUsedMemoryInBytes: 0,
    diagramBackgroundColor: "#f0f0f0",
    diagramTextColor: "#36363a",
    cpuUtilisationColor: "#0d6efd",
    diskUsageColor: "#0d6efd",
    memoryUtilisationColor: "#0d6efd"
  },
  timer: new Timer(),
  resetText: ""
};

export const store = new BehaviorSubject<Model>(initialState);

export function set(recipe: (model: Draft<Model>)=>void) {
  const nextState = produce(store.value, recipe);
  store.next(nextState);
}
