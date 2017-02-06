# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        '''
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
        '''
        "*** YOUR CODE HERE ***"
        return RA_HOOK().evaluation_function(currentGameState, action)
        #return successorGameState.getScore()

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game
        """
        "*** YOUR CODE HERE ***"
        return MA_HOOK(self, gameState).get_action()
        #util.raiseNotDefined()

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        return AA_HOOK(self, gameState).get_action()
        #util.raiseNotDefined()

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """
    #def __init__(self, **param):
    #    MultiAgentSearchAgent.__init__(*param)
    #    self.emht_map = {}
        
    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
        return EA_HOOK(self, gameState).get_action()
        #util.raiseNotDefined()

def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    return BE_HOOK().better_evaluation_function(currentGameState)
    #raw_input()
    #return currentGameState.getScore()
    #util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction


#######################################################
from util import manhattanDistance
from game import Actions
      
class RA_HOOK(object):
    def evaluation_function(self, state, action):
        MINUS_INFINITY, WARN_LENGTH, MIN_SCARED_TIME = -1000000, 2, 2
        
        next = Actions.getSuccessor(state.getPacmanPosition(), action)
        food, caps, all_ghst = state.getFood(), state.getCapsules(), state.getGhostStates()
        ghst = [t.getPosition() for t in all_ghst if t.scaredTimer <= MIN_SCARED_TIME]
        
        weight_food = MINUS_INFINITY if food.count() <= 0 else max([-1 * manhattanDistance(dot, next) for dot in food.asList()])
        weight_caps = MINUS_INFINITY if len(caps) <= 0 else max([-1 * manhattanDistance(dot, next) for dot in caps])
        weight_ghst = -MINUS_INFINITY if len(ghst) <= 0 else min([manhattanDistance(dot, next) for dot in ghst])
        
        score = (weight_food, weight_caps, weight_ghst) if weight_ghst > WARN_LENGTH else (MINUS_INFINITY, MINUS_INFINITY, weight_ghst)
        
        return score

        
class MA_HOOK(object):
    def __init__(self, agent, state):
        self.state, self.agent = state, agent
        
    def min_value(self, state, id, depth):
        legal_actions = state.getLegalActions(id)
        if len(legal_actions) <= 0:
            return self.agent.evaluationFunction(state)
        if id == state.getNumAgents() - 1:
            return min([self.max_value(state.generateSuccessor(id, action), 0, depth+1) for action in legal_actions])
        else:
            return min([self.min_value(state.generateSuccessor(id, action), id+1, depth) for action in legal_actions])
        
    def max_value(self, state, id, depth):
        if self.agent.depth <= depth:
            return self.agent.evaluationFunction(state)
        legal_actions = state.getLegalActions(0)
        if len(legal_actions) <= 0:
            return self.agent.evaluationFunction(state)
        return max([self.min_value(state.generateSuccessor(0, action), 1, depth) for action in legal_actions])
        
    def get_action(self):
        return max([(self.min_value(self.state.generateSuccessor(0, action), 1, 0), action) for action in self.state.getLegalActions(0)])[1]
        

class AA_HOOK(object):
    def __init__(self, agent, state):
        self.state, self.agent = state, agent
        self.INFINITY, self.MINUS_INFINITY = 1*(10**20), -1*(10**20)
        
    def min_value(self, state, id, depth, alpha, beta):
        legal_actions = state.getLegalActions(id)
        if len(legal_actions) <= 0:
            return [self.agent.evaluationFunction(state), 'Stop']
        
        v, a = self.INFINITY, 'Stop'
        for action in legal_actions:
            if id == state.getNumAgents() - 1:
                m = self.max_value(state.generateSuccessor(id, action), 0, depth+1, alpha, beta)
            else:
                m = self.min_value(state.generateSuccessor(id, action), id+1, depth, alpha, beta)
            if v > m[0]:
                v, a = m[0], action
            if v < alpha: 
                return [v, a]
            if v < beta: 
                beta = v
        return [v, a]
            

    def max_value(self, state, id, depth, alpha, beta):
        if self.agent.depth <= depth:
            return [self.agent.evaluationFunction(state), 'Stop']
        legal_actions = state.getLegalActions(0)
        if len(legal_actions) <= 0:
            return [self.agent.evaluationFunction(state), 'Stop']

        v, a = self.MINUS_INFINITY, 'Stop'
        for action in legal_actions:
            m = self.min_value(state.generateSuccessor(0, action), 1, depth, alpha, beta)
            if v < m[0]:
                v, a = m[0], action
            if v > beta: 
                return [v, a]
            if v > alpha: 
                alpha = v
        return [v, a]
        
        
    def get_action(self):
        return self.max_value(self.state, 0, 0, self.MINUS_INFINITY, self.INFINITY)[1]
        
        
class EA_HOOK(object):
    def __init__(self, agent, state):
        self.state, self.agent = state, agent
        
    def expecti_value(self, state, id, depth):
        legal_actions = state.getLegalActions(id)
        if len(legal_actions) <= 0:
            return self.agent.evaluationFunction(state)
        if id == state.getNumAgents() - 1:
            return sum([self.max_value(state.generateSuccessor(id, action), 0, depth+1) for action in legal_actions])/float(len(legal_actions))
        else:
            return sum(([self.expecti_value(state.generateSuccessor(id, action), id+1, depth) for action in legal_actions]))/float(len(legal_actions))
        
    def max_value(self, state, id, depth):
        if self.agent.depth <= depth:
            return self.agent.evaluationFunction(state)
        legal_actions = state.getLegalActions(0)
        if len(legal_actions) <= 0:
            return self.agent.evaluationFunction(state)
        return max([self.expecti_value(state.generateSuccessor(0, action), 1, depth) for action in legal_actions])
        
    def get_action(self):
        return max([(self.expecti_value(self.state.generateSuccessor(0, action), 1, 0), action) for action in self.state.getLegalActions(0)])[1]
                

class BE_HOOK(object):
    def __init__(self):
        self.emht_map = {}
        
    def roll_over(self, x, y0, y1, g, f=False): #f: flag to be inversed
        s = 0
        d = 1 if y0 <= y1 else -1
        for y in xrange(y0, y1, d):
            e = g[x][y] if not f else g[y][x]
            if e: 
                s += 1 
            else: 
                break
        return s
    
    def measure_unidimensional_path(self, x0, y0, x1, y1, g, f=False):  #f: flag to be inversed
        r = 0
        if y0 > y1:
            y0, y1 = y1, y0
        s = y1 - y0
        m = g.height-1 if not f else g.width-1
        
        d = 1 if x0 <= x1 else -1
        for x in xrange(x0, x1, d):
            t = 0
            if s == self.roll_over(x, y0, y1, g, f):
                t = 2 * min(self.roll_over(x, y0, 0, g, f), self.roll_over(x, y1, m, g, f)) 
            if r < t:
                r = t
        return s + r
    
    
    def extended_mht_distance(self, a, b, wall):
        y_len = self.measure_unidimensional_path(a[0], a[1], b[0], b[1], wall, f=False)
        x_len = self.measure_unidimensional_path(a[1], a[0], b[1], b[0], wall, f=True)
        return x_len + y_len

        
    def emht_lookup(self, a, b, wall):
        k = (a[0], a[1], b[0], b[1])
        if k in self.emht_map.keys():
            v = self.emht_map[k]
        else:
            v = self.extended_mht_distance(a, b, wall)
            self.emht_map[k] = v
        return v
    
    def better_evaluation_function(self, state):
        MINUS_INFINITY, WARN_LENGTH, MIN_SCARED_TIME = -10000000, 2.2, 2
        c_food, c_caps = -50, -200
        food, caps, wall = state.getFood(), state.getCapsules(), state.getWalls()
        ghst = [t.getPosition() for t in state.getGhostStates()]
        num_food, num_caps, num_ghst = food.count(), len(caps), len(ghst)
        next = state.getPacmanPosition()
        score = state.getScore()
        
        if num_food > 0:
            #nearest_food_length = max([-1 * manhattanDistance(dot, next) for dot in food.asList()])
            nearest_food_length = max([-1 * self.emht_lookup(dot, next, wall) for dot in food.asList()])
            score += num_food * c_food + nearest_food_length
        if num_caps > 0:
            nearest_caps_length = max([-1 * manhattanDistance(dot, next) for dot in caps])
            score += num_caps * c_caps + nearest_caps_length
        
        if num_ghst > 0:
            nearest_ghst_length = min([manhattanDistance(dot, next) for dot in ghst])
            if nearest_ghst_length < WARN_LENGTH:
                if min([t.scaredTimer for t in state.getGhostStates()]) >= MIN_SCARED_TIME:
                    score += WARN_LENGTH - nearest_ghst_length
                else:
                    score = MINUS_INFINITY + nearest_ghst_length
            else:
                score += 1.0/nearest_ghst_length
        return score
       
        
#########################
###MY CODE ENDS HERE###
