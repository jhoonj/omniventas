import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Formulas } from './formulas';

describe('Formulas', () => {
  let component: Formulas;
  let fixture: ComponentFixture<Formulas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Formulas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Formulas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
