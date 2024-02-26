import { TestBed } from '@angular/core/testing';

import { CacheBusterService } from './cache-buster.service';

describe('CacheBusterService', () => {
  let service: CacheBusterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CacheBusterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
