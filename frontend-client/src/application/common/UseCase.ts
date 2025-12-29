export interface UseCase<T> {
  execute(request: T): Promise<void>;
}
