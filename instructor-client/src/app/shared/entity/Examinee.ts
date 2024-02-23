export class Examinee {
  username: string;
  connected: boolean;

  constructor(username: string, connected: boolean) {
    this.username = username;
    this.connected = connected;
  }
}
