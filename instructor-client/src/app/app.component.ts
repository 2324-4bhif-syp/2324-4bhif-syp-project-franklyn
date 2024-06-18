import {Component, inject, OnInit} from '@angular/core';
import {CommonModule, Location} from '@angular/common';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {environment} from "../../env/environment";
import {StoreService} from "./services/store.service";
import {ExamineeService} from "./services/examinee.service";
import {set, store} from "./model";
import {WebApiService} from "./services/web-api.service";
import {ScheduleService} from "./services/schedule.service";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  protected store = inject(StoreService).store;
  protected examineeSvc = inject(ExamineeService);
  protected webApi = inject(WebApiService);
  protected location = inject(Location);
  protected scheduleSvc = inject(ScheduleService);

  protected readonly environment = environment;

  ngOnInit() {
    this.webApi.getIntervalSpeed();
  }

  resetExaminees(): void {
    this.examineeSvc.resetExaminees();
  }

  resetTextIsWantedText(): boolean {
    return this.store.value.resetText !== environment.wantedResetText
  }

  screenshotCaptureIntervalUpdate(): void {
    this.webApi.updateScreenshotCaptureInterval(this.store.value.timer.screenshotCaptureInterval);
  }

  setResetText(val: string): void {
    set((model) => {
      model.resetText = val;
    });
  }

  setPatrolMode(val: boolean): void {
    set((model) => {
      model.patrol.isPatrolModeOn = val;
    });
  }

  setPatrolSpeed(val: number): void {
    set((model) => {
      model.timer.patrolSpeed = val;
    });
  }

  setNextClientTime(val: number): void {
    set((model) => {
      model.timer.nextClientTime = val;
    });
    this.screenshotCaptureIntervalUpdate();
  }

  setScreenshotCaptureInterval(val: number): void {
    set((model) => {
      model.timer.screenshotCaptureInterval = val;
    });
  }

  public changeRoute() {
    if (this.location.path() === "patrol-mode") {
      this.scheduleSvc.startExamineeScheduleInterval();
      this.scheduleSvc.startPatrolInterval();
    } else {
      this.scheduleSvc.stopExamineeScheduleInterval();
      this.scheduleSvc.stopPatrolInterval();
    }

    set((model) => {
      //safety measure to prevent any possible bugs
      model.patrol.patrolExaminee = undefined;
      model.patrol.isPatrolModeOn = false;
    })
  }

  protected readonly Number = Number;
}
