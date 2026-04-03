import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Solution} from '../../../domain/entities/solution';
import {PageResponse} from '../../../domain/entities/page-response';

@Injectable({
    providedIn: 'root'
})
export class SolutionService {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/solutions';
    private readonly HINTS_API_URL = '/ai-api/api/v1/hints';

    addSolution(solution: { description: string, taskId: number }): Observable<Solution> {
        return this.http.put<Solution>(this.API_URL, solution);
    }

    getSolutionById(id: number): Observable<Solution> {
        return this.http.get<Solution>(`${this.API_URL}/${id}`);
    }

    getSolutionsByTask(taskId: number, page: number, size: number): Observable<PageResponse<Solution>> {
        return this.http.get<PageResponse<Solution>>(`${this.API_URL}/task/${taskId}`, {params: {page, size}});
    }

    getHint(taskDescription: string): Observable<{ hint: string }> {
        return this.http.post<{ hint: string }>(this.HINTS_API_URL, { task: taskDescription });
    }
}
