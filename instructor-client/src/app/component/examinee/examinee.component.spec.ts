import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamineeComponent } from './examinee.component';

describe('ExamineeComponent', () => {
  let component: ExamineeComponent;
  let fixture: ComponentFixture<ExamineeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamineeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExamineeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
