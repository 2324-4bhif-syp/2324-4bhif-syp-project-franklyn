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
export class ScheduleService implements OnInit{
  private store = inject(StoreService).store;
  private examineeRepo = inject(ExamineeService);
  protected webApi = inject(WebApiService);

  ngOnInit(): void {
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
    console.log("stop getting server metrics");

    console.log("stop", this.store.value.timer.serverMetricsTimerId)
    clearInterval(this.store.value.timer.serverMetricsTimerId);
    set((model) => {
      model.timer.serverMetricsTimerId = undefined;
    })
  }

  stopExamineeScheduleInterval() {
    console.log("stop getting examinees");

    if (this.store.value.timer.clientScheduleTimerId !== undefined) {
      clearInterval(this.store.value.timer.clientScheduleTimerId);
    }

    set((model) => {
      model.timer.clientScheduleTimerId = undefined;
    });
  }

  stopPatrolInterval() {
    console.log("stop switching between patrol-examinees");

    if (this.store.value.timer.patrolScheduleTimer !== undefined) {
      clearInterval(this.store.value.timer.patrolScheduleTimer);
    }

    set((model) => {
      model.timer.patrolScheduleTimer = undefined;
    });
  }

  startExamineeScheduleInterval() {
    console.log("start getting examinees from server");

    this.stopExamineeScheduleInterval();

    if (this.store.value.timer.clientScheduleTimerId === undefined) {
      this.store.value.timer.clientScheduleTimerId = setInterval(() => {
        this.examineeRepo.updateScreenshots();
        this.webApi.getExamineesFromServer();
      }, this.store.value.timer.nextClientTimeMilliseconds);
    }
  }

  startPatrolInterval() {
    console.log("start switching between patrol-examinees");

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
    console.log("start getting server-metrics");

    this.stopGettingServerMetrics();

    if (this.store.value.timer.serverMetricsTimerId === undefined) {
      set((model) => {
        model.timer.serverMetricsTimerId = setInterval(() => {
          this.webApi.getServerMetrics();
        }, this.store.value.timer.reloadDashboardIntervalMilliseconds);
      });
    }

    console.log("start", this.store.value.timer.serverMetricsTimerId);
  }
}
