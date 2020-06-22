package evaluation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import filessystem.FileExport;
import filessystem.FileImport;
import model.KMean;
import model.KModifier;
import model.PointGenerator;
import other.DecrementKTests;
import other.DoubleDecrementKTests;
import other.DoubleIncrementKTests;
import other.IncDecTests;
import other.IncrementKTests;
import structures.Clustering;
import tools.Silhouette;
import tools.SilhouetteLight;

/**
 * The main class for the evaluation of the heuristics by performing a special
 * test scheme for each data set and each heuristic.
 */
public class EvaluationCenter {

	private PointGenerator generator;
	private KMean kMean;
	private KModifier kModifier;
	private List<Clustering> tests;

	// temporary variables for testing
	private Clustering unclassified;
	private Clustering test;
	private Clustering initial;
	private long tStart;
	private long tEnd;

	public EvaluationCenter() {
		generator = new PointGenerator();
		kMean = new KMean();
		kModifier = new KModifier(kMean);
	}

	/**
	 * Generate and store 100 random data sets for a given number of points and
	 * the specified number of distributions.
	 * 
	 * @param pointNumber
	 *            the number of points for all data sets
	 * @param differentK
	 *            the number of different distributions
	 */
	public void createRandomData(int pointNumber, int differentK) {
		for (int i = 0; i < differentK; i++) {
			for (int j = i * 100 / differentK; j < (i + 1) * 100 / differentK; j++) {
				FileExport.storeUnclassified(generator.generate(pointNumber, i + 2, 1.5),
						pointNumber + "points_" + (j + 1));
			}
		}
	}

	/**
	 * Test the heuristics that increment the number of clusters by one
	 * independently of the number of distributions. The data sets for the tests
	 * are imported from the text files first. The results are stored in the
	 * corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKIncExt(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KIncExt " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(), IncrementKTests.values(),
				differentK, fileName, "KIncExt", true);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's (1,2 or 3 less than the "real" k, if possible)
			StdResult result = new StdResult(sampleSize, differentK, IncrementKTests.size);
			for (int k = 1; k < differentK + 1; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					tStart = System.nanoTime();
					initial = kMean.cluster(k, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addInitial(k - 1, tEnd - tStart, kMean.getBasicRuntime(), kMean.getTotalIterations());
					// for each test
					for (int l = 0; l < IncrementKTests.size; l++) {
						tStart = System.nanoTime();
						test = kModifier.incrementKEvaluation(initial, IncrementKTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != k + 1) {
							System.err.println("k not matching: expected " + (k + 1) + " , got " + test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k - 1) * IncrementKTests.size + l, tEnd - tStart, kMean.getBasicRuntime(),
								kMean.getTotalIterations(), kMean.getBasicIterations(), silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KIncExt-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * Test the heuristics that increment the number of clusters by one
	 * depending on the number of distributions. The data sets for the tests are
	 * imported from the text files first. The results are stored in the
	 * corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKInc(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KInc " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(), IncrementKTests.values(),
				differentK, fileName, "KInc", true);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's (1,2 or 3 less than the "real" k, if possible)
			int maxK = Math.min(differentK, unclassified.getHiddenK() - 1);
			StdResult result = new StdResult(sampleSize, maxK, IncrementKTests.size);
			for (int k = 1; k < maxK + 1; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					tStart = System.nanoTime();
					initial = kMean.cluster(unclassified.getHiddenK() - k, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addInitial(k - 1, tEnd - tStart, kMean.getBasicRuntime(), kMean.getTotalIterations());
					// for each test combination (3*4+2=15)
					for (int l = 0; l < IncrementKTests.size; l++) {
						tStart = System.nanoTime();
						test = kModifier.incrementKEvaluation(initial, IncrementKTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != unclassified.getHiddenK() - k + 1) {
							System.err.println("k not matching: expected " + (unclassified.getHiddenK() - k + 1)
									+ " , got " + test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k - 1) * IncrementKTests.size + l, tEnd - tStart, kMean.getBasicRuntime(),
								kMean.getTotalIterations(), kMean.getBasicIterations(), silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KInc-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * Test the heuristics that increment the number of clusters by two
	 * depending on the number of distributions. The data sets for the tests are
	 * imported from the text files first. The results are stored in the
	 * corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKIncDouble(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KIncDouble " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(),
				DoubleIncrementKTests.values(), differentK, fileName, "KIncDouble", true);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's (2,3 or 4 less than the "real" k, if possible)
			int maxK = Math.max(0, Math.min(differentK, unclassified.getHiddenK() - 3));
			StdResult result = new StdResult(sampleSize, maxK, DoubleIncrementKTests.size);
			for (int k = 2; k < maxK + 2; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					tStart = System.nanoTime();
					initial = kMean.cluster(unclassified.getHiddenK() - k, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addInitial(k - 2, tEnd - tStart, kMean.getBasicRuntime(), kMean.getTotalIterations());
					// for each test
					for (int l = 0; l < DoubleIncrementKTests.size; l++) {
						tStart = System.nanoTime();
						test = kModifier.incrementKEvaluation(initial, DoubleIncrementKTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != unclassified.getHiddenK() - k + 2) {
							System.err.println("k not matching: expected " + (unclassified.getHiddenK() - k + 2)
									+ " , got " + test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k - 2) * DoubleIncrementKTests.size + l, tEnd - tStart,
								kMean.getBasicRuntime(), kMean.getTotalIterations(), kMean.getBasicIterations(),
								silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KIncDouble-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * Test the heuristics that decrement the number of clusters by one
	 * independently of the number of distributions. The data sets for the tests
	 * are imported from the text files first. The results are stored in the
	 * corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKDecExt(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KDecExt " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(), DecrementKTests.values(),
				differentK, fileName, "KDecExt", true);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's
			StdResult result = new StdResult(sampleSize, differentK, DecrementKTests.size);
			for (int k = 2; k < differentK + 2; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					tStart = System.nanoTime();
					initial = kMean.cluster(k, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addInitial(k - 2, tEnd - tStart, kMean.getBasicRuntime(), kMean.getTotalIterations());
					// for each test
					for (int l = 0; l < DecrementKTests.size; l++) {
						tStart = System.nanoTime();
						test = kModifier.decrementKEvaluation(initial, DecrementKTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != k - 1) {
							System.err.println("k not matching: expected " + (k - 1) + " , got " + test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k - 2) * DecrementKTests.size + l, tEnd - tStart, kMean.getBasicRuntime(),
								kMean.getTotalIterations(), kMean.getBasicIterations(), silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KDecExt-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * Test the heuristics that decrement the number of clusters by one
	 * depending on the number of distributions. The data sets for the tests are
	 * imported from the text files first. The results are stored in the
	 * corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKDec(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KDec " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(), DecrementKTests.values(),
				differentK, fileName, "KDec", true);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's (1,2 or 3 above the "real" k)
			StdResult result = new StdResult(sampleSize, differentK, DecrementKTests.size);
			for (int k = 1; k < differentK + 1; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					tStart = System.nanoTime();
					initial = kMean.cluster(unclassified.getHiddenK() + k, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addInitial(k - 1, tEnd - tStart, kMean.getBasicRuntime(), kMean.getTotalIterations());
					// for each of the 12 test combinations
					for (int l = 0; l < DecrementKTests.size; l++) {
						tStart = System.nanoTime();
						test = kModifier.decrementKEvaluation(initial, DecrementKTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != unclassified.getHiddenK() + k - 1) {
							System.err.println("k not matching: expected " + (unclassified.getHiddenK() + k - 1)
									+ " , got " + test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k - 1) * DecrementKTests.size + l, tEnd - tStart, kMean.getBasicRuntime(),
								kMean.getTotalIterations(), kMean.getBasicIterations(), silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KDec-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * Test the heuristics that decrement the number of clusters by two
	 * depending on the number of distributions. The data sets for the tests are
	 * imported from the text files first. The results are stored in the
	 * corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKDecDouble(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KDecDouble " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(),
				DoubleDecrementKTests.values(), differentK, fileName, "KDecDouble", true);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's (2,3 or 4 above the "real" k)
			StdResult result = new StdResult(sampleSize, differentK, DoubleDecrementKTests.size);
			for (int k = 2; k < differentK + 2; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					tStart = System.nanoTime();
					initial = kMean.cluster(unclassified.getHiddenK() + k, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addInitial(k - 2, tEnd - tStart, kMean.getBasicRuntime(), kMean.getTotalIterations());
					// for each of the 12 test combinations
					for (int l = 0; l < DoubleDecrementKTests.size; l++) {
						tStart = System.nanoTime();
						test = kModifier.decrementKEvaluation(initial, DoubleDecrementKTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != unclassified.getHiddenK() + k - 2) {
							System.err.println("k not matching: expected " + (unclassified.getHiddenK() + k - 2)
									+ " , got " + test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k - 2) * DoubleDecrementKTests.size + l, tEnd - tStart,
								kMean.getBasicRuntime(), kMean.getTotalIterations(), kMean.getBasicIterations(),
								silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KDecDouble-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * Test the selected heuristics that either increment or decrement the
	 * number of clusters by one independently of the number of distributions.
	 * For comparison of incrementing and decrementing. The data sets for the
	 * tests are imported from the text files first. The results are stored in
	 * the corresponding containers and finally exported as an excel file.
	 * 
	 * @param sampleSize
	 *            the number of repetitions of every test
	 * @param differentK
	 *            the number of different values for k that are tested for every
	 *            data set and every heuristic
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 * @param calculateSilhouette
	 *            flag to disable the costly calculation of the silhouette
	 *            values for high point numbers
	 */
	public void evaluateKIncDec(int sampleSize, int differentK, String fileName, boolean calculateSilhouette) {
		long begin = System.currentTimeMillis();
		System.out.println("Started KIncDec " + fileName);
		tests = FileImport.loadTestData(fileName);
		TestContainer results = new TestContainer(sampleSize, tests.get(0).getPointNumber(), IncDecTests.values(),
				2 * differentK - 1, fileName, "KIncDec", false);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each of the basic data
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			// for different k's in both directions (above and under the "real"
			// k)
			int maxK = Math.min(differentK - 1, unclassified.getHiddenK() - 2);
			StdResult result = new StdResult(sampleSize, 2 * differentK - 1, IncDecTests.size);
			for (int k = -maxK; k < differentK; k++) {
				// multiple iterations for statistical significance (average
				// values)
				for (int j = 0; j < sampleSize; j++) {
					// incrementing
					tStart = System.nanoTime();
					initial = kMean.cluster(unclassified.getHiddenK() + k - 1, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addClustering((k + differentK - 1) * IncDecTests.size, tEnd - tStart,
							kMean.getBasicRuntime(), kMean.getTotalIterations(), kMean.getBasicIterations(), 0);
					// for each of the 4 incrementing tests
					for (int l = 1; l < IncDecTests.size / 2; l++) {
						tStart = System.nanoTime();
						test = kModifier.incDecEvaluation(initial, IncDecTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != unclassified.getHiddenK() + k) {
							System.err.println("k not matching: expected " + (unclassified.getHiddenK() + k) + " , got "
									+ test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k + differentK - 1) * IncDecTests.size + l, tEnd - tStart,
								kMean.getBasicRuntime(), kMean.getTotalIterations(), kMean.getBasicIterations(),
								silhouette);
					}
					// decrementing
					tStart = System.nanoTime();
					initial = kMean.cluster(unclassified.getHiddenK() + k + 1, unclassified.lightClone());
					tEnd = System.nanoTime();
					result.addClustering((k + differentK - 1) * IncDecTests.size + 9, tEnd - tStart,
							kMean.getBasicRuntime(), kMean.getTotalIterations(), kMean.getBasicIterations(), 0);
					// for each of the 4 decrementing tests
					for (int l = IncDecTests.size / 2; l < IncDecTests.size - 1; l++) {
						tStart = System.nanoTime();
						test = kModifier.incDecEvaluation(initial, IncDecTests.values()[l]);
						tEnd = System.nanoTime();
						if (test.getK() != unclassified.getHiddenK() + k) {
							System.err.println("k not matching: expected " + (unclassified.getHiddenK() + k) + " , got "
									+ test.getK());
						}
						double silhouette = 0;
						if (calculateSilhouette) {
							silhouette = Silhouette.calculateSilhouetteEmptyClusters(test);
						}
						result.addClustering((k + differentK - 1) * IncDecTests.size + l, tEnd - tStart,
								kMean.getBasicRuntime(), kMean.getTotalIterations(), kMean.getBasicIterations(),
								silhouette);
					}
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("KIncDec-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}

	/**
	 * A special test to determine the number of distribution (ground truth) of
	 * each data set by maximizing the silhouette. The heuristics for
	 * incrementing and decrementing are chosen based on the least runtime. The
	 * data sets for the tests are imported from the text files first. The
	 * results are stored in a special container and exported with a own method
	 * as an excel file.
	 * 
	 * @param differentK
	 *            the number of different initial values for k that are tested
	 *            for every data set
	 * @param fileName
	 *            the file name that specifies the number of points and thus the
	 *            data set that are imported for the test
	 */
	public void evaluateFindK(int differentK, String fileName) {
		long begin = System.currentTimeMillis();
		System.out.println("Started FindK " + fileName);
		tests = FileImport.loadTestData(fileName);
		FindKContainer results = new FindKContainer(tests.get(0).getPointNumber(), fileName, differentK);
		System.out.println("Loaded Data");
		long begin2 = System.currentTimeMillis();
		// for each combination of heuristics
		// for each data set
		for (int i = 0; i < tests.size(); i++) {
			unclassified = tests.get(i);
			FindKResult result = new FindKResult(differentK);
			// for different initial k's
			for (int k = 1; k < differentK + 1; k++) {
				// multiple times for statistical significance
				for (int j = 0; j < 10; j++) {
					initial = kMean.cluster(k, unclassified.lightClone());
					// double maxSilhouetteInc =
					// Silhouette.calculateSilhouette(initial);
					double maxSilhouetteInc = SilhouetteLight.calculateSilhouetteLight(initial);
					double maxSilhouetteDec = maxSilhouetteInc;
					int bestKInc = k;
					int bestKDec = k;
					double silhouette = 0;
					int optimalK = -1;

					Clustering currentClustering = initial;
					boolean endWhile = false;
					while (!endWhile) {
						test = kModifier.incrementKEvaluation(currentClustering, IncrementKTests.INC_DL);
						// silhouette = Silhouette.calculateSilhouette(test);
						silhouette = SilhouetteLight.calculateSilhouetteLight(test);
						if (silhouette > maxSilhouetteInc) {
							maxSilhouetteInc = silhouette;
							currentClustering = test;
							bestKInc++;
						} else {
							endWhile = true;
						}
					}

					currentClustering = initial;
					endWhile = false;
					while (bestKDec > 1 && !endWhile) {
						test = kModifier.decrementKEvaluation(currentClustering, DecrementKTests.DEC_C);
						// silhouette = Silhouette.calculateSilhouette(test);
						silhouette = SilhouetteLight.calculateSilhouetteLight(test);
						if (silhouette > maxSilhouetteDec) {
							maxSilhouetteDec = silhouette;
							currentClustering = test;
							bestKDec--;
						} else {
							endWhile = true;
						}
					}

					if (maxSilhouetteInc > maxSilhouetteDec) {
						optimalK = bestKInc;
					} else {
						optimalK = bestKDec;
					}

					int hit = -1;
					if (unclassified.getHiddenK() - optimalK == 0) {
						hit = 1;
					} else {
						hit = 0;
					}
					result.addData(k - 1, Math.max(maxSilhouetteInc, maxSilhouetteDec), optimalK, hit);
				}
			}
			results.addTest(result);
			System.out.print((i + 1) + "% ");
		}
		long end2 = System.currentTimeMillis();
		System.out.println();
		System.out.println("FindK-Tests completed in " + (end2 - begin2) / 1000 + " s.");
		results.exportFindKTests();

		long end = System.currentTimeMillis();
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
				+ " : Finished in " + (end - begin) / 1000 + " s.");
		System.out.println();
	}
}
