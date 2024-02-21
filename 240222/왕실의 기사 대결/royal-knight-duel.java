import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class Main {

	static int L, N, Q; // 체스판 크기, 기사의 수, 명령의 수
	static int[][] map;
	static Knight[] knights;

	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());

		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());

		map = new int[L + 1][L + 1];
		knights = new Knight[N + 1];
		int[] maxHp = new int[N + 1];
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			knights[i] = new Knight(r, c, h, w, k);
			maxHp[i] = knights[i].hp;
		}

		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			int kNum = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			if (knights[kNum].hp > 0)
				move(kNum, d);
		}
		int res = 0;
		for (int i = 1; i <= N; i++) {
			if (knights[i].hp > 0)
				res += maxHp[i] - knights[i].hp;
		}

		System.out.println(res);

	}

	static class Knight {
		int row, col, height, width, hp, dmg;
		boolean isUsed;

		public Knight(int row, int col, int height, int width, int hp) {
			this.row = row;
			this.col = col;
			this.height = height;
			this.width = width;
			this.hp = hp;
			dmg = 0;
			isUsed = false;
		}

	}

	static void move(int knightIdx, int d) {
		if (canMove(knightIdx, d)) {
			for (int i = 1; i <= N; i++) {
				if (knights[i].isUsed) {
					knights[i].row += dr[d];
					knights[i].col += dc[d];
					knights[i].hp -= knights[i].dmg;
				}
				knights[i].isUsed = false;
				knights[i].dmg = 0;
			}
		} else {
			for (int i = 1; i <= N; i++) {
				knights[i].isUsed = false;
				knights[i].dmg = 0;
			}
		}
	}

	static boolean canMove(int knightIdx, int d) {
		Deque<Integer> queue = new ArrayDeque<>();

		queue.add(knightIdx);
		knights[knightIdx].isUsed = true;
		while (!queue.isEmpty()) {
			int idx = queue.poll();
			int row = knights[idx].row + dr[d];
			int col = knights[idx].col + dc[d];
			int height = knights[idx].height;
			int width = knights[idx].width;

			if (!isValidRange(row, col, height, width))
				return false;

			for (int i = row; i < row + height; i++) {
				for (int j = col; j < col + width; j++) {
					if (map[i][j] == 1) {
						knights[idx].dmg++;
					} else if (map[i][j] == 2) {
						return false;
					}
				}
			}

			for (int i = 1; i <= N; i++) {
				if (knights[i].hp <= 0 || knights[i].isUsed || knights[i].row > row + height - 1
					|| knights[i].row + knights[i].height - 1 < row
					|| knights[i].col > col + width - 1
					|| knights[i].col + knights[i].width - 1 < col) {
					continue;
				}
				queue.add(i);
				knights[i].isUsed = true;

			}
		}
		knights[knightIdx].dmg = 0;
		return true;
	}

	static boolean isValidRange(int r, int c, int h, int w) {
		return r >= 1 && r + h - 1 <= L && c >= 1 && c + w - 1 <= L;
	}
}