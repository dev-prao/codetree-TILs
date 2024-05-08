import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {

	static int N, M, K, C, result;

	static int[][][] map;
	//[0] 나무 그루 수, [1] 제초제 범위 내 나무 그루 수, [2] 제초제 남은 기한
	static int[] dr = {-1, 0, 0, 1, -1, -1, 1, 1};
	static int[] dc = {0, -1, 1, 0, -1, 1, -1, 1};

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		//input
		N = Integer.parseInt(st.nextToken()); //격자 크기
		M = Integer.parseInt(st.nextToken()); //박멸 진행 년 수
		K = Integer.parseInt(st.nextToken()); //제초제 확산 범위
		C = Integer.parseInt(st.nextToken()); //제초제 남아있는 년 수

		//map info, 0: 빈칸, -1: 벽
		map = new int[3][N][N];
		for (int r = 0; r < N; r++) {
			st = new StringTokenizer(br.readLine());
			for (int c = 0; c < N; c++) {
				map[0][r][c] = Integer.parseInt(st.nextToken());
			}
		}
		for (int year = 0; year < M; year++) {
			simulation();
		}

		System.out.println(result);
	}

	static void simulation() {
		grow();
		spread();
		getBestSpot();
	}

	/*
	1. 인접한 네 개의 칸 중에 나무가 있는 칸의 수만큼 나무가 성장
	성장은 모든 나무가 동시에 일어남
	 */
	static void grow() {
		int[][] tmp = new int[N][N];
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (map[0][r][c] <= 0) continue;
				for (int d = 0; d < 4; d++) {
					int nr = r + dr[d];
					int nc = c + dc[d];
					if (isOutOfMap(nr, nc)) continue;
					if (map[0][nr][nc] > 0) tmp[r][c]++;
				}
			}
		}

		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				map[0][r][c] += tmp[r][c];
			}
		}
	}

	/*
	2. 기존에 있었던 나무들은 인접한 4개의 칸 중 벽, 다른 나무, 제초제 모두 없는 칸에 번식 진행
	각 칸의 나무 그루 수에서 총 번식이 가능한 칸의 개수만큼 나누어진 그루 수만큼 번식, 나머지 버림
	 */
	static void spread() {
		int[][] tmp = new int[N][N];
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (map[0][r][c] == 0 || map[0][r][c] == -1) {
					tmp[r][c] = 0;
					continue;
				}
				int cnt = 0;
				for (int d = 0; d < 4; d++) {
					int nr = r + dr[d];
					int nc = c + dc[d];
					if (isOutOfMap(nr, nc)) continue;
					if (map[0][nr][nc] == 0 && map[2][nr][nc] == 0) cnt++;
				}
				if (cnt == 0) continue;
				tmp[r][c] = map[0][r][c] / cnt;
			}
		}

		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (map[0][r][c] == 0 && map[2][r][c] == 0) {
					for (int d = 0; d < 4; d++) {
						int nr = r + dr[d];
						int nc = c + dc[d];
						if (isOutOfMap(nr, nc)) continue;
						map[0][r][c] += tmp[nr][nc];
					}
				}
			}
		}
	}

	/*
	3. 제초제를 뿌렸을 때 나무가 가장 많이 박멸되는 칸에 제초제 뿌림
	제초제는 k의 범위만큼 대각선으로 퍼짐
	나무가 없는 칸에 제초제를 뿌리면 박멸되는 나무 전혀 없는 상태로 끝
	나무가 있는 칸에 제초제를 뿌리면 4개의 대각선 방향으로 k칸만큼 전파
	전파되는 도중 벽 또는 나무 없는 칸 -> 그 칸까지 제초제 뿌려지며 이후 칸으로 전파 x
	제초제가 뿌려진 칸에는 c년만큼 제초제가 남아있다가 c+1년째가 될 때 사라짐
	제초제가 뿌려진 곳에 다시 제초제가 뿌려지는 경우 c년 추가
	 */
	static void getBestSpot() {
		int maxR = 0;
		int maxC = 0;
		int maxTree = 0;
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				boolean[] isBlocked = new boolean[4];
				map[1][r][c] = map[0][r][c];
				if (map[0][r][c] == 0 || map[0][r][c] == -1) continue;
				for (int lv = 1; lv <= K; lv++) {
					for (int d = 4; d < 8; d++) {
						if(isBlocked[d - 4]) continue;
						int nr = r + dr[d] * lv;
						int nc = c + dc[d] * lv;
						if (isOutOfMap(nr, nc)) continue;
						if (map[0][nr][nc] == 0 || map[0][nr][nc] == -1) {
							isBlocked[d - 4] = true;
							continue;
						}
						map[1][r][c] += map[0][nr][nc];
					}
				}
				if(map[1][r][c] > maxTree) {
					maxTree = map[1][r][c];
					maxR = r;
					maxC = c;
				}
			}
		}

		result += maxTree;

		map[0][maxR][maxC] = 0;
		map[2][maxR][maxC] += C;
		boolean[] isBlocked = new boolean[4];
		for (int lv = 1; lv <= K; lv++) {
			for (int d = 4; d < 8; d++) {
				if(isBlocked[d - 4]) continue;
				int nr = maxR + dr[d] * lv;
				int nc = maxC + dc[d] * lv;
				if (isOutOfMap(nr, nc)) continue;
				map[0][nr][nc] = 0;
				map[2][nr][nc] += C;
				if (map[0][nr][nc] == 0 || map[0][nr][nc] == -1) isBlocked[d - 4] = true;
			}
		}
	}

	static boolean isOutOfMap(int r, int c) {
		return r < 0 || c < 0 || r >= N || c >= N;
	}

	static void print(int idx) {
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				System.out.printf("%4d", map[idx][r][c]);
			}
			System.out.println();
		}
	}
}