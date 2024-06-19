import {inject, Injectable, OnDestroy, OnInit} from '@angular/core';
import {StoreService} from "./store.service";
import {set} from "../model";
import {ExamineeService} from "./examinee.service";
import {distinctUntilChanged, map} from "rxjs";
import {environment} from "../../../env/environment";
import {WebApiService} from "./web-api.service";

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private store = inject(StoreService).store;
  private examineeRepo = inject(ExamineeService);
  protected webApi = inject(WebApiService);

  constructor() {
    this.store.pipe(
      map(model => model.timer.patrolSpeed),
      distinctUntilChanged()
    ).subscribe(() => {
      this.startPatrolInterval();
    })

    this.store.pipe(
      map(model => model.timer.nextClientTime),
      distinctUntilChanged()
    ).subscribe(() => {
      this.startExamineeScheduleInterval();
    })
  }

  stopGettingServerMetrics() {
    clearInterval(this.store.value.timer.serverMetricsTimerId);
    set((model) => {
      model.timer.serverMetricsTimerId = undefined;
    })
  }

  stopExamineeScheduleInterval() {
    if (this.store.value.timer.clientScheduleTimerId !== undefined) {
      clearInterval(this.store.value.timer.clientScheduleTimerId);
    }

    set((model) => {
      model.timer.clientScheduleTimerId = undefined;
    });
  }

  stopPatrolInterval() {
    if (this.store.value.timer.patrolScheduleTimer !== undefined) {
      clearInterval(this.store.value.timer.patrolScheduleTimer);
    }

    set((model) => {
      model.timer.patrolScheduleTimer = undefined;
    });
  }

  startExamineeScheduleInterval() {
    this.stopExamineeScheduleInterval();

    if (this.store.value.timer.clientScheduleTimerId === undefined) {
      this.store.value.timer.clientScheduleTimerId = setInterval(() => {
        this.examineeRepo.updateScreenshots();
        if (this.store.value.examData.curExam)
          this.webApi.getExamineesFromServer(this.store.value.examData.curExam.id);
      }, this.store.value.timer.nextClientTimeMilliseconds);
    }
  }

  startPatrolInterval() {
    this.stopPatrolInterval();

    if (this.store.value.timer.patrolScheduleTimer === undefined) {
      set((model) => {
        model.timer.patrolScheduleTimer = setInterval(() => {
          this.examineeRepo.newPatrolExaminee();
        }, this.store.value.timer.patrolSpeedMilliseconds);
      });
    }
  }

  startGettingServerMetrics() {
    this.stopGettingServerMetrics();

    if (this.store.value.timer.serverMetricsTimerId === undefined) {
      set((model) => {
        model.timer.serverMetricsTimerId = setInterval(async () => {
          await this.webApi.getServerMetrics();
        }, this.store.value.timer.reloadDashboardIntervalMilliseconds);
      });
    }
  }
}
