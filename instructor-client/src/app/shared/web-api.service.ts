import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {ExamineeDto} from "./entity/ExamineeDto";
import {timeout} from "rxjs/operators";
import {environment} from "../../../env/environment";

@Injectable({
  providedIn: 'root'
})
export class WebApiService {
  headers: HttpHeaders = new HttpHeaders().set('Accept', 'application/json');

  constructor(private http: HttpClient) { }

  public getClientsFromServer() {
    return this.http.get<ExamineeDto[]>(environment.serverBaseUrl, {headers: this.headers});
  }

  public getOpenBoxImage(ip: string) {
    return this.http.get<number[]>(
      `http://${ip}:${environment.openboxPort}/${environment.openboxImageUrl}/health`,
      {headers: this.headers}
    ).pipe(timeout(2000));
  }
}
