import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Examinee, ServerMetrics} from "../model";
import {environment} from "../../../env/environment";
import {firstValueFrom} from "rxjs";
import {set} from "../model/model";

@Injectable({
  providedIn: 'root'
})
export class WebApiService {
  private httpClient = inject(HttpClient);
  private headers: HttpHeaders = new HttpHeaders().set('Accept', 'application/json');

  public async getExamineesFromServer(): Promise<void> {
    const examinees: Examinee[] = await firstValueFrom(
      this.httpClient
      .get<Examinee[]>(
        `${environment.serverBaseUrl}/examinees`,
        {headers: this.headers}
      )
    );

    set((model) => {
      model.examineeData.examinees = examinees;
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
    const intervalSpeed: number = await firstValueFrom(
      this.httpClient
        .get<number>(
          `${environment.serverBaseUrl}/screenshot/intervalSpeed`,
      {headers: this.headers}
        )
    );

    set((model) => {
      model.examineeData.getExamineeIntervalSpeed = intervalSpeed;
    });
  }

  public async getServerMetrics(): Promise<void> {
    const serverMetrics: ServerMetrics = await firstValueFrom(
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
