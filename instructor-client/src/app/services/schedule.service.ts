import {inject, Injectable, OnDestroy, OnInit} from '@angular/core';
import {StoreService} from "./store.service";
import {set} from "../model";
import {ExamineeService} from "./examinee.service";
import {distinctUntilChanged, map} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ScheduleService implements OnDestroy, OnInit{
  private store = inject(StoreService).store;
  private examineeRepo = inject(ExamineeService);

  ngOnInit(): void {
    this.store.pipe(
      map(model => model.patrol.nextPatrol),
      distinctUntilChanged()
    ).subscribe(() => {
      this.startPatrolInterval();
    })

    this.store.pipe(
      map(model => model.patrol.nextClientTime),
      distinctUntilChanged()
    ).subscribe(() => {
      this.startExamineeScheduleInterval();
    })
  }

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
      this.store.value.patrol.clientTimer = setInterval(() => {
        this.examineeRepo.updateScreenshots();
      }, this.store.value.patrol.nextClientTimeUnformatted);
    }
  }

  startPatrolInterval() {
    this.stopPatrolInterval();

    if (this.store.value.patrol.patrolTimer === undefined) {
      this.store.value.patrol.patrolTimer = setInterval(() => {
        this.examineeRepo.newPatrolExaminee();
      }, this.store.value.patrol.nextPatrolUnformatted);
    }
  }

  ngOnDestroy(): void {
    this.stopExamineeScheduleInterval();
    this.stopPatrolInterval();
  }
}
