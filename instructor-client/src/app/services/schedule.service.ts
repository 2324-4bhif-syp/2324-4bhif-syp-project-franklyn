import {inject, Injectable} from '@angular/core';
import {StoreService} from "./store.service";
import {set} from "../model";
import {ExamineeService} from "./examinee.service";

@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private store = inject(StoreService).store;
  private examineeRepo = inject(ExamineeService);

  stopExamineeScheduleInterval() {
    if (this.store.value.patrol.clientTimer !== undefined) {
      clearInterval(this.store.value.patrol.clientTimer);
    }

    set((model) => {
      model.patrol.clientTimer = undefined;
    });
  }

  stopPatrolInterval() {
    if (this.store.value.patrol.patrolTimer !== undefined) {
      clearInterval(this.store.value.patrol.patrolTimer);
    }

    set((model) => {
      model.patrol.patrolTimer = undefined;
    });
  }

  startExamineeScheduleInterval() {
    this.stopExamineeScheduleInterval();

    if (this.store.value.patrol.clientTimer === undefined) {
      this.store.value.patrol.clientTimer = window.setInterval(() => {
        this.examineeRepo.updateScreenshots();
      }, this.store.value.patrol.nextClientTime);
    }
  }

  startPatrolInterval() {
    this.stopPatrolInterval();

    if (this.store.value.patrol.patrolTimer === undefined) {
      this.store.value.patrol.patrolTimer = window.setInterval(() => {
        this.examineeRepo.newPatrolExaminee();
      }, this.store.value.patrol.nextPatrol);
    }
  }
}
