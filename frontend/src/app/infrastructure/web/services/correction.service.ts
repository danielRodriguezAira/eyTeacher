import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Correction} from '../../../domain/entities/correction';

@Injectable({
    providedIn: 'root'
})
export class CorrectionService {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/corrections';

    addCorrection(correction: Correction): Observable<Correction> {
        return this.http.put<Correction>(this.API_URL, correction);
    }
}
