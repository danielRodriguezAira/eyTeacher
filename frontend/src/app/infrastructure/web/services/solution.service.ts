import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Solution} from '../../../domain/entities/solution';

@Injectable({
    providedIn: 'root'
})
export class SolutionService {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/solutions';

    addSolution(solution: { description: string, taskId: number }): Observable<Solution> {
        return this.http.put<Solution>(this.API_URL, solution);
    }
}
