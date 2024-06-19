import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoExamineeComponent } from './video-examinee.component';

describe('VideoExamineeComponent', () => {
  let component: VideoExamineeComponent;
  let fixture: ComponentFixture<VideoExamineeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideoExamineeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VideoExamineeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
