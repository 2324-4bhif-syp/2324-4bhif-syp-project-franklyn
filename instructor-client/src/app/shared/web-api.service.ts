import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environment/environment";
import {ExamineeDto} from "./entity/ExamineeDto";
import {timeout} from "rxjs/operators";

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
    return this.http.get<number[]>(`http://${ip}:${environment.openboxPort}/${environment.openboxImageUrl}/health`, {headers: this.headers});
  }
}
