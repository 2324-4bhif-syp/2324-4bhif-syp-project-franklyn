import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Examine} from "./entity/Examine";
import {environment} from "../../environment/environment";

@Injectable({
  providedIn: 'root'
})
export class WebApiService {
  headers: HttpHeaders = new HttpHeaders().set('Accept', 'application/json');
  constructor(private http: HttpClient) { }

  public getClientsFromServer() {
    return this.http.get<Examine[]>(environment.serverBaseUrl, {headers: this.headers});
  }
}
