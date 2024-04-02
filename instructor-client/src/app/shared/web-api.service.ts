import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../env/environment";
import {Examinee} from "./entity/Examinee";
import {ServerMetrics} from "./entity/ServerMetrics";

@Injectable({
  providedIn: 'root'
})
export class WebApiService {
  headers: HttpHeaders = new HttpHeaders().set('Accept', 'application/json');

  constructor(private http: HttpClient) { }

  public getClientsFromServer() {
    return this.http.get<Examinee[]>(`${environment.serverBaseUrl}/examinees`, {headers: this.headers});
  }

  public resetExaminees(): void {
    this.http.post(`${environment.serverBaseUrl}/state/reset`, {}).subscribe();
  }

  public updateScreenshotCaptureInterval(updateInterval: number): void {
    this.http.post(`${environment.serverBaseUrl}/screenshot/updateInterval`,
      {newInterval: updateInterval}
    ).subscribe();
  }

  public getIntervalSpeed() {
    return this.http.get<number>(`${environment.serverBaseUrl}/screenshot/intervalSpeed`,
      {headers: this.headers});
  }

  public getServerMetrics(){
    return this.http.get<ServerMetrics>(
      `${environment.serverBaseUrl}/state/system-metrics`,
      {headers: this.headers}
    );
  }
}
