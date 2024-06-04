import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamineeDownloadListComponent } from './examinee-download-list.component';

describe('ExamineeDownloadListComponent', () => {
  let component: ExamineeDownloadListComponent;
  let fixture: ComponentFixture<ExamineeDownloadListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamineeDownloadListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExamineeDownloadListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
