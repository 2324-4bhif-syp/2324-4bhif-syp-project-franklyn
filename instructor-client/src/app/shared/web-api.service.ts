import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../../env/environment";
import {Examinee} from "./entity/Examinee";

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
}
