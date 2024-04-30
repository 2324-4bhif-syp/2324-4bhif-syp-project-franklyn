import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Examinee, ServerMetrics} from "../model";
import {environment} from "../../../env/environment";
import {lastValueFrom} from "rxjs";
import {set} from "../model";

@Injectable({
  providedIn: 'root'
})
export class WebApiService {
  private httpClient = inject(HttpClient);
  private headers: HttpHeaders = new HttpHeaders().set('Accept', 'application/json');

  public async getExamineesFromServer(): Promise<void> {
      this.httpClient.get<Examinee[]>(
        `${environment.serverBaseUrl}/examinees`,
        {headers: this.headers})
        .subscribe({
        "next": (examinees) => set((model) => {
          model.examineeData.examinees = examinees;
        }),
        "error": (err) => console.error(err),
      });
  }

  public async resetServer(): Promise<void> {
    this.httpClient.post(
      `${environment.serverBaseUrl}/state/reset`,
      {}
    ).subscribe();
  }

  public async updateScreenshotCaptureInterval(updateInterval: number): Promise<void> {
    this.httpClient.post(
      `${environment.serverBaseUrl}/screenshot/updateInterval`,
      {newInterval: updateInterval}
    ).subscribe();
  }

  public async getIntervalSpeed(): Promise<void> {
    const intervalSpeed: number = await lastValueFrom(
      this.httpClient
        .get<number>(
          `${environment.serverBaseUrl}/screenshot/intervalSpeed`,
      {headers: this.headers}
        )
    );

    set((model) => {
      model.timer.screenshotCaptureInterval = intervalSpeed;
    });
  }

  public async getServerMetrics(): Promise<void> {
    const serverMetrics: ServerMetrics = await lastValueFrom(
      this.httpClient
        .get<ServerMetrics>(
        `${environment.serverBaseUrl}/state/system-metrics`,
        {headers: this.headers}
      )
    );

    set((model) => {
      model.serverMetrics = serverMetrics;
    });
  }
}
